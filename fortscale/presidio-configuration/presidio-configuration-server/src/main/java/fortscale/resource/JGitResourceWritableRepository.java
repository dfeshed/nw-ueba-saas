package fortscale.resource;


import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.cloud.config.server.environment.JGitEnvironmentRepository;
import org.springframework.cloud.config.server.environment.SearchPathLocator;
import org.springframework.cloud.config.server.support.AbstractScmAccessor;
import org.springframework.core.io.UrlResource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;

public class JGitResourceWritableRepository extends GenericResourceWritableRepository {

    private static final String ADD_FILE_MESSAGE = "add configuration file: %s by configuration server";

    private SearchPathLocator service;
    private JGitEnvironmentRepository gitAccessor;
    private JGitResourceWritableRepository.JGitFactory gitFactory = new JGitResourceWritableRepository.JGitFactory();

    public JGitResourceWritableRepository(SearchPathLocator service,  JGitEnvironmentRepository gitAccessor) {
        super(service);
        this.service = service;
        this.gitAccessor = gitAccessor; // TODO: make sure that cloneOnStart is on

    }

    @Override
    public String store(String application, String profile, String label, boolean override, String content) throws IOException {

        String fullPath = super.store(application, profile, label, override, content);
        Git git = createGitClient();
        add(git, fullPath);
        commit(git, String.format(ADD_FILE_MESSAGE, application);
        return fullPath;
    }


    private void add(Git git, String pathToAdd) {
        AddCommand add = git.add();
        try {
            add.addFilepattern(pathToAdd).call();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    private void commit(Git git, String message) {
        CommitCommand commit = git.commit();
        try {
            commit.setMessage(message).call();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }



    private Git createGitClient() throws IOException {
        if (!new File(getWorkingDirectory(), ".git").exists()) {
            //TODO: throw exception?
            throw  new IllegalStateException("cannot open git " + getWorkingDirectory());
        }
        return openGitRepository();
    }

    private File getWorkingDirectory() {
        if (gitAccessor.getUri().startsWith("file:")) {
            try {
                return new UrlResource(StringUtils.cleanPath(gitAccessor.getUri())).getFile();
            }
            catch (Exception e) {
                throw new IllegalStateException(
                        "Cannot convert uri to file: " + gitAccessor.getUri());
            }
        }
        return gitAccessor.getBasedir();
    }


    private Git openGitRepository() throws IOException {
        Git git = this.gitFactory.getGitByOpen(getWorkingDirectory());
        return git;
    }


    static class JGitFactory {

        public Git getGitByOpen(File file) throws IOException {
            Git git = Git.open(file);
            return git;
        }
    }

    }
