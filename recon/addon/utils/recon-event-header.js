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
  nweProcessParentFilename: 150,
  nweProcessPath: 155,
  nweDllFilename: 160,
  nweDllPath: 165,
  nweDllProcessFilename: 170,
  nweAutorunFilename: 175,
  nweAutorunPath: 180,
  nweServiceDisplayName: 185,
  nweServiceFilename: 190,
  nweServicePath: 195,
  nweTaskName: 200,
  nweTaskPath: 205,
  nweNetworkFilename: 210,
  nweNetworkPath: 215,
  nweNetworkProcessFilename: 220,
  nweNetworkProcessPath: 225,
  nweNetworkRemoteAddress: 230
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