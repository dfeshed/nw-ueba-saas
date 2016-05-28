# Bower

Currently Ember CLI uses [NPM](npm) for "server-side" dependencies, and [Bower](bower) for "client-side"
dependencies.  Using Bower is an issue because inside the Tier-2 environment, GSO prevents us from issuing
any HTTP `POST` to `github.com` to prevent data exfiltration.  This limits our ability to use Bower
in that environment.

However, Stefan Penner announced at EmberConf 2016, that Ember will be transitioning away from
Bower in his talk [Ember CLI, The Next Generation](emberconf).

## Bower Registry Artifact

This artifact is a bridge to allow us to use Bower in Tier-2, until all the Bower dependencies
can be removed from our products.  It is not built by default (i.e., it's not included as a
module in the `client/pom.xml`) and must be manually built.

This artifact works by packaging the local cache of the Bower registry, packages and links.  When run
inside Tier-2, Bower is configured to use this cache, and otherwise operate offline, preventing any
external requests.

### Requirements
1. This artifact's `bower.json` should contain **ALL** the Bower dependencies for the Netwitness UI, and
any additional addons (component-lib, style-guide, etc)
1. All bower-dependencies should have an explicit version pinned, such as `3.2.1`, and not use any
[Semantic Version Ranges](semver): `~3.2.1`, `^3.2.1`, etc

### Updating Dependencies

When any of the Bower dependencies get updated, this artifact must be manually built on a developer's
local machine, and the resulting artifact uploaded to [Artifactory](artifactory).

To build, invoke Maven from the `bower-registry` project directory:

```bash
$ cd client/bower-registry
$ mvn
```

After completion, the `target/bower-registry-11.0.0.0-SNAPSHOT.jar` file must be uploaded to
[Artifactory](artifactory).  You must be logged-in, and have the required deployment permission to
upload to Artifactory.

### Running in Tier-2

In order to run in Tier-2, the `tier2` Maven profile should be added to the build jobs:

```
-U clean verify -Ptier2
```

### Running in Development

There are no changes needed to developers existing workflow.


[npm]: https://www.npmjs.com/
[bower]: http://bower.io/
[emberconf]: https://youtu.be/UMo9DHrRccI?list=PL4eq2DPpyBblc8aQAd516-jGMdAhEeUiW&t=786
[artifactory]: http://repo1.rsa.lab.emc.com:8081/artifactory/webapp/home.html?1
[semver]: https://docs.npmjs.com/misc/semver
