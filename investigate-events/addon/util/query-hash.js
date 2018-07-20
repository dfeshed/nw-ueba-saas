export const createQueryHash = function(service, startTime, endTime, pills) {
  const pillHash = pills.map((p) => {
    return `${p.meta}-${p.operator}-${p.value}-${p.complexFilterText}`;
  }).join('-');
  return `${service}-${startTime}-${endTime}-${pillHash}`;
};