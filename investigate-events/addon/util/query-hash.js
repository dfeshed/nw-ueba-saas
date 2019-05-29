export const createQueryHash = function(service, startTime, endTime, pills) {
  const pillHash = pills.map((p) => {
    return `${(p.meta) ? p.meta : undefined}-${(p.operator) ? p.operator : undefined}-${(p.value) ? p.value : undefined}-${(p.complexFilterText) ? p.complexFilterText : undefined}-${(p.searchTerm) ? p.searchTerm : undefined}`;
  }).join('-');
  return `${service}-${startTime}-${endTime}-${pillHash}`;
};