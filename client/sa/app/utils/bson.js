export default {
  /**
   * Converts text from BSON syntax to JSON syntax.
   * Not comprehensive yet; just sufficient for the MongoDB BSON that we export for testing.
   * @see https://docs.mongodb.org/manual/reference/mongodb-extended-json/
   * @returns {string}
   * @public
   */
  toJson(txt, isArray) {
    txt = txt || '';
    txt = txt.trim();

    // Wrap the entire BSON array in square brackets, if missing.
    if (isArray) {
      if ((txt.charAt(0) !== '[') || (txt.charAt(txt.length - 1) !== ']')) {
        txt = `[${txt}]`;
      }
    }

    // Insert commas between records.
    return txt.replace(/\}\s*\{/gm, '\},\{')    // Inserts commas between records.
      .replace(/\"\_id\"/gm, '\"id\"')          // Replaces "_id" with "id".
      .replace(/\{ \"\$date\" \: (\d+) \}/gm, function($0, $1) {   // Replaces { "$date" : ## } with ##.
        return $1;
      })
      .replace(/ISODate\(\"(\S+)\"\)/gm, function($0, $1) {       // Replaces ISODate(string) with Date.parse(string).
        return Date.parse($1);
      })
      .replace(/\{ \"\$numberLong\" \: \"?(\d+)\"? \}/gm, function($0, $1) {   // Replaces { "$numberLong" : ## } with ##.
        return $1;
      })
      .replace(/NumberLong\(\"?(\d+)\"?\)/gm, function($0, $1) {        // Replaces NumberLong(##) with ##.
        return $1;
      })
      .replace(/\{ \"\$oid\" \: (\S+) \}/, function($0, $1) {        // Replaces { "$oid" : X } with X.
        return $1;
      });
  }
};
