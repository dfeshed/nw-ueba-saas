/**
 * @description utility file that holds helper functions
 */

 /**
 * @function parsePostData
 * @param query {string} eg key1=value1&key2=value2 or key1=val!1&key2=val@123
 * @return result {object} {key1: "value1", key2: "value2"} or {key1: "val!1", key2: "val@123"}
 */
export function parsePostData( query ){
    var result = {};
    query.split("&").forEach (function(part){
        var item = part.split("=");
        result[item[0]] = decodeURIComponent(item[1]);
    });
    return result;
}

export default {
    parsePostData: parsePostData
};
