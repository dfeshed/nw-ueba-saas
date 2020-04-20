import Helper from '@ember/component/helper';

// Generates an SVG 'translate' transform string for a given x, y.
export function svgTranslation(x, y) {
  return `translate(${Number(x || 0).toFixed(1)} ${Number(y || 0).toFixed(1)})`;
}

export default Helper.helper(function([x, y]) {
  return svgTranslation(x, y);
});
