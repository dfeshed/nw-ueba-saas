/**
 * A list of objects that define all the supported error codes received from
 * server API calls. Each object has the following properties:
 * ```
 * code:Number - The code returned by the server
 * description:String - Document purpose of code
 * isFatal:Boolean - The error has wide UI impact
 * isContent:Boolean - The error is localized to content
 * ```
 * @type {object[]}
 * @private
 */
const _ERROR_CODES = [
  {
    code: 115,
    description: 'Session unavailable',
    isFatal: true,
    isContent: false
  },
  {
    code: 124,
    description: 'Invalid sessionId',
    isFatal: true,
    isContent: false
  },
  {
    code: 11,
    description: 'SessionId too long',
    isFatal: true,
    isContent: false
  }
];

/**
 * Enumeration of fatal error codes from API calls.
 * @return {object[]} List of fatal error codes.
 * @public
 */
const FATAL_ERROR_CODES = _ERROR_CODES
  .filter((error) => error.isFatal)
  .map((error) => error.code);

/**
 * A generic error code. The intended use is to handle cases where API calls
 * fail, but do not return an error code. This should, in theory, not happen.
 * @type { number }
 * @default 999
 * @public
 */
const GENERIC_API_ERROR_CODE = 999;

export {
  FATAL_ERROR_CODES,
  GENERIC_API_ERROR_CODE
};