export const WHERE_CRITERIA = 'WHERE_CRITERIA';
export const GROUP = 'GROUP';
export const CRITERIA = 'CRITERIA';
export const META_VALUE_RANGE = 'META_VALUE_RANGE';
export const META_VALUE = 'META_VALUE';
export const TEXT_QUERY = 'TEXT_QUERY';
// Catch-all for things produced by the parser that cause errors, but aren't
// big enough errors to cause the whole thing to crash. These structures should
// be immediately turned into complex pills once they leave the parser.
export const COMPLEX_FILTER = 'COMPLEX_FILTER';
