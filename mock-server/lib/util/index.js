import { createConnectMessage, parseMessage, createMessage, createSubscriptionReceiptMessage } from './message';
import { discoverSubscriptions, subscriptionList } from './subscriptions';
import mockAuthResponse from './mock-auth-response';

const determineDelay = (configuredDelayForResponse) => {
  // if an endpoint has a configured delay, use that
  if (configuredDelayForResponse) {
    return configuredDelayForResponse;
  }

  // use configured value or default of 2000
  const delayMax = process.env.RESPONSE_DELAY || 2000;

  // if coming from command line, is string
  const delay = parseInt(delayMax, 10);

  // return random number between 0 and max
  return Math.floor(Math.random() * Math.floor(delay));
};

export {
  createConnectMessage,
  parseMessage,
  createMessage,
  createSubscriptionReceiptMessage,
  discoverSubscriptions,
  subscriptionList,
  mockAuthResponse,
  determineDelay
};