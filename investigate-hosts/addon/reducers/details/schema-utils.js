import _ from 'lodash';

/**
 * Signature property tells is file has valid signature or not. For unsigned file server will not sends the signature
 * property. So adding signature key for all the files and setting it to undefined if key is not there in the response
 * @param fileProperties
 * @returns {undefined}
 * @private
 */
const _getSignature = ({ fileProperties }) => {
  return (fileProperties && fileProperties.signature) ? fileProperties.signature.features : undefined;
};

/**
 * Adding unique id for the record
 * @param data
 * @param fileId
 * @param key
 * @returns {*}
 * @public
 */
const addId = (data, fileId, key, addAdditionalKeys) => {
  if (data && data.length) {
    data.forEach((item) => {
      item.id = _.uniqueId(key);
      item.fileId = fileId;
      if (typeof(addAdditionalKeys) === 'function') {
        addAdditionalKeys(item);
      }
    });
  }
};
/**
 * Common Normalizer Strategy for adding the parent property to each child, in th response autoruns, drivers, lib are
 * under the property <machineOsType> : []. While displaying the data we need to show parent property also in te table
 *
 * File context data has the json structure like
 *  fileContext: {
 *    machineOsType: 'linux'
 *    path: '',
 *    owner: '',
 *    timeModified: '',
 *    checksumSha256: '',
 *    fileProperties: {
 *      fileName: '',
 *      fileHash: ''
 *    }
 *    linux: {
 *      'drivers': [
 *        {
 *         'imageSize': ,
 *         'numberOfInstances': 0,
 *         'loadState': 'Live',
 *       },
 *       {
 *         'imageSize': ,
 *         'numberOfInstances': 0,
 *         'loadState': 'Live',
 *       }
 *     ]
 *    }
 *  }
 *  Out put should be
 *
 *  drivers: [
 *   {
 *    fileProperties: { }
 *    path: '',
 *    owner: '',
 *    timeModified: '',
 *    checksumSha256: '',
 *    'imageSize': ,
 *    'numberOfInstances': 0,
 *    'loadState': 'Live',
 *   }
 *  ]
 * @param input
 * @param parent
 * @returns object
 * @public
 */

const commonNormalizerStrategy = (input, parent) => {
  const signature = _getSignature(parent);
  const parentClone = { ...parent };
  const { machineOsType } = parentClone;
  if (parentClone[machineOsType]) {
    const machineOsContent = Object.keys(parentClone[machineOsType]);
    // Removing the os based objects and arrays created from the os based object's content from the clone.
    delete parentClone[machineOsType];
    if (machineOsContent.length) {
      machineOsContent.forEach((item) => {
        delete parentClone[item];
      });
    }
  }
  return { ...parentClone, ...input, signature };
};
export {
  addId,
  commonNormalizerStrategy
};