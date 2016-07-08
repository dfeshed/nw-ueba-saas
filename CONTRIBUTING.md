# Contributing to Security Analytics UI

Not sure what a pull request is, or how to submit one? Take a look at GitHub's
excellent [help documentation](https://help.github.com/articles/using-pull-requests/) first.

## Fork the SA-UI repository

The SA-UI repository uses the "fork and pull" model. This model lets anyone 
fork an existing repository and push changes to their personal fork without 
requiring access be granted to the source repository. The changes must then 
be pulled into the SA-UI repository by the Core UI team.

## Create a Branch

### Branch from `master`

Master currently represents work toward the next version of the Security 
Analytics UI. Please submit all pull requests there, even bug fixes and 
minor improvements.

### Branch names

Branches used when submitting pull requests should be all lowercase, with 
dashes for word separators and have a prefix:

- feature/asoc-ABCD-brief-name-vM - Used for feature/user-story development.
  ABCD is the JIRA ticket and vM is a version number .
- feature/asoc-ABCD-brief-name-fixes - Used for minor bug fixes on same area 
  that can be bundled together into one feature branch. In this case ABCD is 
  either the epic user story or a user story followed by a brief name of the 
  area of the work. We are going with this approach as it is getting to be 
  too many builds for Q.E to test, if we give one build for each bug fix off 
  of the bug feature branch.
 
## Follow code and style guidelines

Please ensure all code adheres to the guidelines for each language:

- [JavaScript](https://github.rsa.lab.emc.com/asoc/launch-libraries/blob/master/conventions/web/javascript-guidelines.md) 
- [HTML](https://github.rsa.lab.emc.com/asoc/launch-libraries/blob/master/conventions/web/html-guidelines.md)
- [CSS](https://github.rsa.lab.emc.com/asoc/launch-libraries/blob/master/conventions/web/css-guidelines.md)

## Prepare Your Commit

### Submit test cases for all behavior changes

Any new feature, bug fix or behavioral change must have sufficient test
coverage.  This includes jUnit tests for Java code, and qUnit tests for
JavaScript code.

### Squash commits

Use `git rebase --interactive --autosquash`, `git add --patch`, and other tools
to "squash" multiple commits into a single atomic commit. In addition to the man
pages for git, there are many resources online to help you understand how these
tools work. The [Rewriting History section of Pro Git](http://git-scm.com/book/en/v2/Git-Tools-Rewriting-History) 
provides a good overview.


### Use real name in git commits

Please configure git to use your real first and last name for any commits you
intend to submit as pull requests. For example, this is not acceptable:

    Author: thiela

Rather, please include your first and last name, properly capitalized:

    Author: First Last <first.last@rsa.com>

This goes a long way to ensuring useful output from tools like 
`git shortlog` and others.

You can configure this via the account admin area in GitHub (useful for
fork-and-edit cases); _globally_ on your machine with

    git config --global user.name "First Last"
    git config --global user.email first.last@rsa.com

or _locally_ for the `sa-ui` repository only by omitting the
'--global' flag:

    cd sa-ui
    git config user.name "First Last"
    git config user.email first.last@rsa.com


### Format commit messages

Please read and follow the [Commit Guidelines section of Pro Git](http://git-scm.com/book/en/v2/Distributed-Git-Contributing-to-a-Project#Commit-Guidelines).

Most importantly, please format your commit messages in the following way
(adapted from the commit template in the link above):

    Short (50 chars or less) summary of changes

    Issue before the fix:
    More detailed explanatory text, if necessary. Wrap it to about 72
    characters or so. In some contexts, the first line is treated as the
    subject of an email and the rest of the text as the body. The blank
    line separating the summary from the body is critical (unless you omit
    the body entirely); tools like rebase can get confused if you run the
    two together.
    
    What changed:
    Some text describing the changes contained in this changeset.
    
    How things work now:
    Describe any new process/workflow changes as a result of this feature 
    or changeset.

    Further paragraphs come after blank lines.

     - Bullet points are okay, too

     - Typically a hyphen or asterisk is used for the bullet, preceded by a
       single space, with blank lines in between, but conventions vary here

    Issue: ASOC-1234, ASOC-1235


1. Use imperative statements in the subject line, e.g. "Fix broken Javadoc link".
   This should **not** just be the Jira issue identifier
1. Begin the subject line with a capitalized verb, e.g. "Add, Prune, Fix,
    Introduce, Avoid, etc."
1. Do not end the subject line with a period
1. Restrict the subject line to 50 characters or less if possible
1. Wrap lines in the body at 72 characters or less
1. Mention associated JIRA issue(s) at the end of the commit comment, prefixed
    with "Issue: " as above
1. In the body of the commit message, explain how things worked before this
    commit, what has changed, and how things work now

## Final steps

### Run all tests prior to submission

TODO - Link to building section of the README.md

### Submit your pull request

Follow GitHub's [pull request guide](https://help.github.com/articles/using-pull-requests/#sending-the-pull-request)
to submit the PR.

### Mention your pull request on the associated JIRA issue

Add a comment to the associated JIRA issue(s) linking to your new pull request.

### Expect discussion and review

Every pull request will be reviewed by a member of the Core UI team.  You 
may be asked to rework the submission for style (as explained above) and/or 
substance.

Note that you can always force push (`git push -f`) reworked / rebased commits
against the branch used to submit your pull request. In other words, you do not
need to issue a new pull request when asked to make changes.

