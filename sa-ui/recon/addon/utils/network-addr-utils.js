const IP_PORT_SEPARATOR = ' : ';

const NETWORK_ADDRESS_TYPES = ['source', 'destination', 'deviceIp', 'nweMachineIp', 'nweNetworkRemoteAddress'];

/**
 * Checks if the name belongs to one of the Network address types like Source, Destination, etc. Typically network
 * addresses have values in the format - ipaddr : port
 * @param name Name of the header item to check if it's a type of network address
 * @returns {boolean} true if it belongs to one of the types mentioned in NETWORK_ADDRESS_TYPES
 * @public
 */
const isNetworkAddress = function(name) {
  return NETWORK_ADDRESS_TYPES.includes(name);
};

/**
 * Extracts the meta and value from the "IP address" part of the key-value passed, and returns an object
 * @param key Combination of IP and port metas separated by a colon - e.g.,  ip.src : port.src
 * @param value Combination of IP and port values separated by a colon - e.g.,  10.1.1.1 : 8080
 * @returns {{metaName: *, metaValue: *, displayValue: *}}
 * @public
 */
const getIpAddressMetaValue = function(key, value) {
  const [metaName] = key.split(IP_PORT_SEPARATOR);
  let [ipValue] = value.split(IP_PORT_SEPARATOR);
  const isIpv6 = ipValue.startsWith('[') && ipValue.endsWith(']');
  if (isIpv6) {
    ipValue = ipValue.substring(1, ipValue.length - 1);
  }
  return { metaName, metaValue: ipValue, displayValue: ipValue };
};

/**
 * Extracts the meta and value from the "port" part of the key-value passed, and returns an object
 * @param key Combination of IP and port metas separated by a colon - e.g.,  ip.src : port.src
 * @param value Combination of IP and port values separated by a colon - e.g.,  10.1.1.1 : 8080
 * @returns {{metaName: *, metaValue: *, displayValue: *}}
 * @public
 */
const getPortMetaValue = function(key, value) {
  const tokenizedKey = key.split(IP_PORT_SEPARATOR);
  const tokenizedValue = value.split(IP_PORT_SEPARATOR);
  if (tokenizedValue.length > 1) {
    return {
      metaName: tokenizedKey[1],
      metaValue: tokenizedValue[1],
      displayValue: ` : ${tokenizedValue[1]}`
    };
  }
  return null;
};

export {
  isNetworkAddress,
  getIpAddressMetaValue,
  getPortMetaValue
};