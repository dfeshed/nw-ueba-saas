const ERROR_CODES = [
  {
    code: 124,
    description: 'Invalid Session Id',
    isFatal: true,
    isContent: false
  }
];

const FATAL_ERROR_CODES = ERROR_CODES.filter((error) => error.isFatal).map((error) => error.code);

// This is just a placeholder for recon's apiFatalErrorCode, so that the value doesn't remain 0, when there is no
// response code fetched from api call.
const GENERIC_API_ERROR = 999;

export {
  FATAL_ERROR_CODES,
  GENERIC_API_ERROR
};