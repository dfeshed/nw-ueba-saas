/**
 * @description List of apis that has to go through xhr instead of mirage
 * @author Srividhya Mahalingam
 */

export default function(config) {
    config.pretender.get('/vendor/incidents.json', config.pretender.passthrough);
}
