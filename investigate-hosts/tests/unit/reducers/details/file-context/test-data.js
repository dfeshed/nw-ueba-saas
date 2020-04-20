const libraryObject = {};
export const libraryTestData = (length) => {
  for (let i = 1; i <= length; i++) {
    libraryObject[`${i}`] = {
      id: `${i}`,
      checksumSha256: i,
      fileProperties: { fileStatus: 'blacklist' }
    };
  }
  return libraryObject;
};