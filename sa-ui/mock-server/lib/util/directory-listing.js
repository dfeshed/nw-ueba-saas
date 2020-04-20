import fs from 'fs';
import path from 'path';

const getRootFolderUrl = function(folder) {
  const withoutSlashes = folder.replace(/\//g, '');
  return `/${withoutSlashes}/`;
};

const getStaticFile = function(folder, originalUrl) {
  const rootFolderUrl = getRootFolderUrl(folder);
  const re = new RegExp(rootFolderUrl, 'g');
  const staticFileName = originalUrl.replace(re, '');
  const staticFilePath = path.join(__dirname, '..', '..', folder, staticFileName);
  try {
    return fs.readFileSync(staticFilePath);
  } catch (e) {
    return null;
  }
};

const staticFileContent = function(staticFileContent, response) {
  if (staticFileContent) {
    response.setHeader('Content-Type', 'text/javascript');
    response.send(staticFileContent);
  } else {
    response.status(404);
    response.send('File Not Found');
  }
};

function dirListing(folder) {
  return function(req, res, next) {
    const originalUrl = req.originalUrl;
    if (originalUrl !== getRootFolderUrl(folder)) {
      const fileContents = getStaticFile(folder, originalUrl);
      return staticFileContent(fileContents, res);
    } else {
      const folderPath = path.normalize(path.join(__dirname, '..', '..', folder));

      fs.stat(folderPath, function(err, stat) {
        if (err && err.code === 'ENOENT') {
          return next();
        }

        if (!stat.isDirectory()) {
          return next();
        }

        fs.readdir(folderPath, function(err, files) {
          if (err) {
            return next(err);
          }
          dirListing.json(req, res, files);
        });
      });
    }
  };
}

dirListing.json = function(req, res, files) {
  const listing = files.map(function(name) {
    const size = Buffer.byteLength(name, 'utf8');
    return {
      name,
      size
    };
  });
  res.json(listing);
};

export {
  dirListing
};
