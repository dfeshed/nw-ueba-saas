/**
 * @file MockServer message handler that responds to DISCONNECT messages.
 * Responds with a RECEIPT frame with a matching receipt id, so that STOMP client will think disconnect was successful.
 */

/**
 * Adds a message handler for DISCONNECT messages. The handler always responds with a RECEIPT.
 * @param {Object} server The MockServer instance, which is expected to have the following methods:
 * "addMessageHandler" and "sendFrame".
 */
export default function (server) {

    server.addMessageHandler(function(message, frames) {

        if (frames[0] && frames[0].command === "DISCONNECT") {
            this.sendFrame(
                "RECEIPT",
                {"receipt-id": frames[0].headers.receipt || ""}
            );
            return true;
        }
        return false;
    });

}
