/**
 * @public
 * @description This object describes what will be displayed on recon event header.
 * @type object
 */
const RECON_DISPLAYED_HEADER = {
  nwService: 0,
  sessionId: 5,
  source: 15,
  destination: 20,
  service: 25,
  firstPacketTime: 30,
  lastPacketTime: 35,
  packetSize: 40,
  payloadSize: 45,
  packetCount: 50,
  deviceIp: 55,
  deviceType: 60,
  deviceClass: 65,
  eventCategory: 70,
  collectionTime: 75,
  eventTime: 80
};

const HAS_TOOLTIP = ['packetSize', 'payloadSize', 'packetCount'];

/**
 * @public
 * @description Get header item from a header item object.
 * @param {object} headerItems Header item object.
 * @param {string} item Item you are searching for.
 */
const getHeaderItem = (headerItems, item) => headerItems ? headerItems.find((d) => d.id === item) || {} : {};

export {
  RECON_DISPLAYED_HEADER,
  HAS_TOOLTIP,
  getHeaderItem
};