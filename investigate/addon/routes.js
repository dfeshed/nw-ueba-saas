import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.mount('investigate-events', { path: 'events' });
  this.mount('investigate-files', { path: 'files' });
  this.mount('investigate-hosts', { path: 'hosts' });
  this.mount('investigate-process-analysis', { path: 'process-analysis' });
  this.route('recon');
});
