export default (paramObject, paramDescription) => {
  if (paramDescription[0] === '<') {
    const endMetaIndex = paramDescription.indexOf('>');
    const meta = paramDescription.substring(1, endMetaIndex).split(', ');
    paramDescription = paramDescription.substring(endMetaIndex + 2);
    for (let i = 0; i < meta.length; i++) {
      paramObject = parseMetaOption(paramObject, meta[i]);
    }
  }
  paramObject.description = paramDescription;
  return paramObject;
};

const parseMetaOption = (paramObject, metaString) => {
  if (metaString.substring(0, 10) === '{enum-one:') {
    paramObject.acceptableValues = metaString.substring(10, metaString.length - 1).split('|');
    paramObject.type = 'enum-one';
  } else if (metaString.substring(0, 10) === '{enum-any:') {
    paramObject.acceptableValues = metaString.substring(10, metaString.length - 1)
      .split('|').map((item) => ({ name: item, code: item }));
    paramObject.type = 'enum-any';
  }
  return paramObject;
};