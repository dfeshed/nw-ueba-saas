import jsesc from 'jsesc';
import Stomp from 'stompjs';
import merge from 'merge';

/*
 * Utility function to prepare the headers for a SEND stomp message
 */
const _createSendHeader = function(subscription, frame) {
  return {
    'destination': subscription.requestDestination,
    'content-type': 'application/json;charset=UTF-8',
    'subscription': frame.headers.id
    // 'message-id': '6edw3evl-0', // not sure what this is for
  };
};

/*
 * Prepares message containing a body. This message is the crux of the response.
 */
const _createMessageWithBody = function(subscription, frame, body) {
  // create headers
  const headers = _createSendHeader(subscription, frame);
  // Stringify body
  const bodyStringified = JSON.stringify(body);
  // Stomp it
  const sendMessage = Stomp.Frame.marshall('MESSAGE', headers, bodyStringified);
  // Sock it
  const sendMessageFull = `a["${jsesc(sendMessage.slice(0, -1))}\\u0000"]`;
  // Mangle it so that the body payload is properly escaped
  const outMsg = sendMessageFull.replace(bodyStringified, bodyStringified.replace(/"/g, '\\"'));
  return outMsg;
};

/*
 * Takes incoming sock/stomp message and parses it to get the
 * command being executed and the frame of data out of it
 */
const parseMessage = function(msg) {
  const parsedMessage = JSON.parse(msg);
  const unmarshalledMessage = Stomp.Frame.unmarshall(parsedMessage[0]);
  const [ frame ] = unmarshalledMessage;
  const { command } = frame;
  return { frame, command };
};

/*
 * Connect messages are generally the same, so this generic message
 * can be returned for all socket requests.
 *
 * Prepares proper connect headers and turns
 * it into a proper Stomp/Sock message
 */
const createConnectMessage = function() {
  const connectHeaders = {
    version: '1.1',
    'heart-beat': '0,0',
    'user-name': 'admin'
  };

  // Stomp it
  const marshalledMsg = Stomp.Frame.marshall('CONNECTED', connectHeaders, null);
  // Sock it
  const msg = `a["${jsesc(marshalledMsg.slice(0, -1))}\\u0000"]`;
  return msg;
};

/*
 * Builds body of message and then calls _createMessageWithBody.
 *
 * If this is called with a body, that body is used.
 *
 * If no body is provided, then subscription.message is called in order
 * to retrieve the body
 */
const createMessage = function(subscription, frame, body = null) {
  const defaultBody = {
    code: 0,
    request: JSON.parse(frame.body)
  };
  const subscriptionBody = body || subscription.message(frame);

  // merging subscriptionBody over default body,
  // gives ability to override defaults in subscription
  const finalBody = merge(defaultBody, subscriptionBody);

  return _createMessageWithBody(subscription, frame, finalBody);
};

export {
  createConnectMessage,
  parseMessage,
  createMessage
};