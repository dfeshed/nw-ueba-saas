/**
 * @description populates the records for /api/devices API.
 * @public
 */

export default function(server) {
  // Do a create call and pass the json that needs to be created
  server.create('devices', { 'username': 'admin','deviceType': 'LOG_DECODER','deviceVersion': '10.4.0.2.3360','displayName': 'LAB-SA-LOGDECODER - Log Decoder' });
  server.create('devices', { 'username': null,'deviceType': 'BROKER','deviceVersion': '10.4.0.2.3360','displayName': 'SA-LAB-BROKER - Broker' });
  server.create('devices', { 'username': 'admin','deviceType': 'CONCENTRATOR','deviceVersion': '10.4.0.2.3360','displayName': 'LAB-SA-CONCENTRATOR - Concentrator' });
  server.create('devices', { 'username': null,'deviceType': 'DECODER','deviceVersion': '10.4.0.2.3360','displayName': 'LAB-SA-PACKETDECODER - Decoder' });
}
