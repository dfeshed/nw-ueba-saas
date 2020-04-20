import Helper from '@ember/component/helper';

// Generates an SVG 'rotate' transform string for a given degree.
export function svgRotation(deg) {
  return `rotate(${deg.toFixed(1)})`;
}

export default Helper.helper(function([deg]) {
  return svgRotation(deg);
});
