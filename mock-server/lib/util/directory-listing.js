import fs from 'fs';
import path from 'path';

function dirListing(folder) {
  return function(req, res, next) {
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
