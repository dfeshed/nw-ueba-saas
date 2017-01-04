// Call this script passing the location of the locale folders
// you would like to check. Locations should be absolute or
// relative to the folder you are in when you execute the script
//
// i.e. node check-translations.js /path/to/sa/app/locales
//
// This script will process any translations in those files
// and verify all the keys match and nothing has been missed

const fs = require('fs-extra');
const path = require('path');
const pluginPath = path.join(__dirname, 'node_modules/babel-plugin-transform-es2015-modules-commonjs');
require('babel-register')({
  plugins: [pluginPath]
});

const localeFolders = process.argv.splice(2);

const objectKeysRecursive = function iterate(obj, stack, keys) {
  Object.keys(obj).forEach((k) => {
    if (typeof obj[k] === 'object') {
      if (stack.length === 0) {
        objectKeysRecursive(obj[k], k, keys);
      } else {
        objectKeysRecursive(obj[k], `${stack}.${k}`, keys);
      }
    } else {
      if (stack.length === 0) {
        keys.push(k);
      } else {
        keys.push(`${stack}.${k}`);
      }
    }
  });
};

const errorMessages = [];

localeFolders
  // First ensure folders exist
  .map((folder) => {
    const folderPath = path.join(process.cwd(), folder);
    const folderExists = fs.existsSync(folderPath);
    if (!folderExists) {
      console.error('Folder passed into translation checking does not exist: ', f, folderPath);
      process.exit(1);
    }
    return {
      folderPath,
      originalFolder: folder
    };
  })
  // Now build a list the various translation files for each folder
  .map((folder) => {
    return fs.walkSync(folder.folderPath).filter((file) => {
      return file.endsWith('trans-data.js');
    }).map((transDataFile) => {
      const tdfPathPieces = transDataFile.split(path.sep);
      const translations = require(transDataFile);
      return {
        originalFolder: folder.originalFolder,
        translationFile: transDataFile,
        language: tdfPathPieces[tdfPathPieces.length - 2],
        translations
      };
    });
  })
  // Go through each translations object and create array of full object key paths
  .map((folder) => {
    return folder.map((language) => {
      const keys = [];
      objectKeysRecursive(language.translations, '', keys);
      language.keys = keys;
      return language;
    });
  })
  // Now compare each set of language keys to the others
  .forEach((folder) => {
    // for each language
    folder.forEach((language) => {
      const inspectingKeys = language.keys;
      const inspectingLanguage = language.language;

      folder
        // get list of langauges that are not the one currently being processed
        .filter((_language) => {
          return _language.language != inspectingLanguage;
        })
        // now look through each of the other langauges
        // and find keys missing there that exist in the
        // language that we are inspecting
        .forEach((_language) => {
          inspectingKeys.forEach((k) => {
            if (_language.keys.indexOf(k) === -1) {
              // Remove the default. that comes post transpile of
              // JSON files
              if (k.indexOf('default.') === 0) {
                k = k.replace('default.', '');
              }
              errorMessages.push(`
                  Key: ${k}
          from Folder: ${language.originalFolder}
        from Language: ${inspectingLanguage}
missing from Language: ${_language.language}`);
            }
          });
        });
    });
  });

console.log('*************************');
if (errorMessages.length > 0) {
  console.log('*************************');
  console.log('Translation mismatches detected');
  console.error(errorMessages.join('\n'));
  console.log('*************************');
  process.exit(1);
} else {
  console.log('No translation mismatches detected');
  console.log('*************************');
}

