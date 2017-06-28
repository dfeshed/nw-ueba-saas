/**
 * @public
 * @description This object describes what will be displayed on recon event header.
 * @type object
 */
const RECON_DISPLAYED_HEADER = {
  nwService: 0,
  sessionId: 5,
  nweCallbackId: 10,
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
  nweCategory: 75,
  collectionTime: 80,
  eventTime: 85,
  nweEventTime: 90,
  nweMachineName: 95,
  nweMachineIp: 100,
  nweMachineUsername: 105,
  nweMachineIiocScore: 110,
  nweEventSourceFilename: 115,
  nweEventSourcePath: 120,
  nweEventDestinationFilename: 125,
  nweEventDestinationPath: 130,
  nweFileFilename: 135,
  nweFileIiocScore: 140,
  nweProcessFilename: 145,
  nweProcessPath: 150,
  nweDllFilename: 155,
  nweDllPath: 160,
  nweAutorunFilename: 165,
  nweAutorunPath: 170,
  nweServiceDisplayName: 175,
  nweServiceFilename: 180,
  nweServicePath: 185,
  nweTaskName: 190,
  nweTaskPath: 195,
  nweNetworkFilename: 200,
  nweNetworkPath: 205,
  nweNetworkProcessFilename: 210,
  nweNetworkProcessPath: 215,
  nweNetworkRemoteAddress: 220
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