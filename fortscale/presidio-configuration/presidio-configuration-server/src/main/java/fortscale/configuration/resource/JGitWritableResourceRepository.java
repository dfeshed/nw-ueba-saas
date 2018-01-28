package fortscale.configuration.resource;


import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.cloud.config.server.environment.JGitEnvironmentRepository;
import org.springframework.cloud.config.server.environment.RepositoryException;
import org.springframework.cloud.config.server.environment.SearchPathLocator;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * An {@link WritableResourceRepository} backed by a single git repository.
 */
public class JGitWritableResourceRepository extends GenericWritableResourceRepository {

    private static final String ADD_FILE_MESSAGE = "add configuration file: %s by configuration server REST api";

    private SearchPathLocator searchPathLocator;
    private JGitEnvironmentRepository gitAccessor;
    private JGitWritableResourceRepository.JGitFactory gitFactory = new JGitWritableResourceRepository.JGitFactory();

    public JGitWritableResourceRepository(SearchPathLocator searchPathLocator, JGitEnvironmentRepository gitAccessor) {
        super(searchPathLocator);
        this.searchPathLocator = searchPathLocator;
        this.gitAccessor = gitAccessor;
    }

    @Override
    public synchronized void store(String application, String profile, String label, String extension, InputStreamSource inputResource) {

        // store to local repository (native file system)
        super.store(application, profile, label, extension, inputResource);

        String fileName = getPropertyFileName(application, profile, extension);
        String fullPath = Paths.get(getWorkingDirectory().getAbsolutePath(), fileName).toString();

        // commit to git
        try {
            Git git = createGitClient();
            add(git, fullPath);
            commit(git, String.format(ADD_FILE_MESSAGE, fullPath));
            logger.debug(String.format("File %s has been commited to the git repostory.", fileName));
        } catch (IOException ex){
            logger.error("Cannot open configuration server git", ex);
            throw new RepositoryException("Cannot open configuration server git", ex);
        } catch (GitAPIException ex) {
            logger.error("Cannot commit into git configuration server", ex);
            throw new RepositoryException(String.format("Cannot commit %s into git configuration server", fullPath), ex);
        }
    }

    private void add(Git git, String pathToAdd) throws GitAPIException {
        AddCommand add = git.add();
        add.addFilepattern(pathToAdd).call();
    }

    private void commit(Git git, String message) throws GitAPIException {
        CommitCommand commit = git.commit();
        commit.setMessage(message).call();
    }

    private Git createGitClient() throws IOException {
        if (!new File(getWorkingDirectory(), ".git").exists()) {
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
