/**
 * Converts radians to degrees. Useful for SVG helpers, since SVG requires degrees.
 * @example  `2 * Math.PI` converts to `360` (degrees)
 * @param {number} rad Radians
 * @returns {number} degrees
 * @public
 */
export default function mathRadToDeg(rad) {
  return rad * 180 / Math.PI;
}
