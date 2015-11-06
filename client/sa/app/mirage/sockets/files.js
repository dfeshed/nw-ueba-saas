/**
 * @file MockServer message handler for subscribing & unsubscribing to JSON data from files.
 * Simulates a stream of data objects. Assumes the JSON is an object with an Array property called "data".
 */
import Ember from "ember";

/**
 * Maps socket URLs to files.
 * Each entry in this Array is an object with the following properties:
 * "regex": a regular expression which is used to match the socket URL;
 * "file": either a file path+name String, or a function that will return a file path+name.  The function will be
 * passed the result of the regular expression match() call.
 * @type {Object[]}
 * @private
 */
var _FILE_MAP = [
    {regex: /\/ws\/threats\/incidents/, file: "/vendor/incidents.json"}
];

/**
 * Determines the file name (if any) that corresponds to the given subscription destination.
 * @param {String} dest The destination for the subscription.
 * @returns {String} filename (e.g., "/vendor/incidents.json")
 * @private
 */
function _fileForDestination(dest) {
    var file;
    if (dest) {
        for (var i = 0, len = _FILE_MAP.length; i < len; i++) {
            var match = dest.match(_FILE_MAP[i].regex);
            if (match) {
                file = _FILE_MAP[i].file;
                if (typeof file === "function") {
                    file = file(match);
                }
                if (file) {
                    break;
                }
            }
        }
    }
    return file;
}

/**
 * The max number of incidents to send in a single chunk ("message") over websocket.
 * @type {number}
 * @private
 */
var _RECORDS_PER_CHUNK = 2;

/**
 * MockServer message handler that responds to SUBSCRIBE & UNSUBSCRIBE messages for certain destinations
 * by loading a JSON array of data from a file, in chunks. Uses an interval to stream the data in chunks.
 * @param {Object} server The MockServer instance, which is expected to have the following methods:
 * "addMessageHandler" and "sendFrame".
 */
export default function (server) {

    // Handler for SUBSCRIBE request.
    server.addMessageHandler(function(message, frames) {

        // Does the requested destination map to any file?
        var firstFrame = frames && frames[0];
        if (firstFrame && firstFrame.command === "SEND") {
            var requestedDestination = (firstFrame && firstFrame.headers.destination),
                file = _fileForDestination(requestedDestination);

            // Cache the subscription ID; for later re-use when unsubscribing.
            var subId = firstFrame.headers.id,
                hash = this._subsForFiles = (this._subsForFiles || {});
            hash[subId] = {file: file, interval: null};

            // Load the data from a JSON file.
            var me = this;
            Ember.$.ajax({
                type: "GET",
                url: file,
                dataType: "json"
            })
                .done(function (response) {

                    // JSON file loaded. Send the array contents into socket in chunks.
                    var records = response || [],
                        recordsToSend = {
                            "code" : 0,
                            "meta" : response.meta,
                            "request": firstFrame.body ? JSON.parse(firstFrame.body) : null
                        };
                    hash[subId].interval = window.setInterval(function(){
                        recordsToSend.data = records.data.splice(0, _RECORDS_PER_CHUNK);
                        me.sendFrame(
                            "MESSAGE",
                            {
                                "subscription": subId,
                                "content-type": "application/json"
                            },
                            JSON.stringify(
                                recordsToSend
                            )
                        );

                        // Stop after sending the entire array.
                        if (!records.data.length) {
                            window.clearInterval(hash[subId].interval);
                            hash[subId].interval = null;
                        }
                    }, 0);
                })
                .fail(function (ret) {

                    // Failure loading the JSON file.
                    // @todo Replace window.alert with html modal dialog.
                    window.alert("Unable to load data.\nAn unexpected error occurred when fetching data.");
                    console.error("Unexpected error loading mock JSON file:\n", file, ret);
                });

            // Let other message handler code know that we've covered this message.
            return true;
        }
        return false;
    });

    server.addMessageHandler(function(message, frames) {

        var firstFrame = frames[0];
        if (firstFrame && firstFrame.command === "UNSUBSCRIBE") {

            // Consult cache to see if the given subscription ID is for the threats topic.
            var subId = firstFrame.headers.id,
                hash = this._subsForFiles || {};
            if (hash[subId]) {

                // Stop the stream for that subscription.
                if (hash[subId].interval) {
                    window.clearTimeout(hash[subId].interval);
                }

                // Remove that subscription ID from the cache.
                delete hash[subId];

                // Let other message handler code know that we've covered this message.
                return true;
            }
        }
        return false;
    });

}
