import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.route('permission-denied');
  this.route('hosts', { path: '/' }, function() {
    this.route('details', { path: '/:id' }, function() {
      this.route('tab', { path: '/:tabName' }, function() {
        this.route('info', { path: '/:rowId' });
        this.route('mft', { path: '/:mftName/:mftFile' });
        this.route('fileanalysis', { path: '/:fileHash/:fileFormat/:fileSid' });
      });
    });
  });
});
