import appConfig from 'sa/config/environment';

/**
 * @description List of apis that has to go through xhr instead of mirage
 * @public
 */

export default function(config) {
  config.pretender.get('/vendor/incident.json', config.pretender.passthrough);
  config.pretender.get('/vendor/alerts.json', config.pretender.passthrough);
  config.pretender.post('/write-blanket-coverage', config.pretender.passthrough);
  config.pretender.get('/vendor/context.json', config.pretender.passthrough);
  config.pretender.get(`http://localhost:${appConfig.mockPort}/socket/info`, config.pretender.passthrough);
}
