/**
 * due to API changes, response returned is in different format than before
 * used to be [name, value][], now { name, type, value, count }[]
 * format data array to match previous format
 *
 * @param {*} data array of meta objects { name, type, value, count }[]
 */
const formatResponse = (data) => {
  if (data) {
    const formatted = [{ metas: [] }];
    if (data.length) {
      data.forEach((meta) => {
        const metaObjectToArray = [ meta.name, meta.value ];
        formatted[0].metas.push(metaObjectToArray);
      });
    }
    return formatted;
  }
};

export {
  formatResponse
};
