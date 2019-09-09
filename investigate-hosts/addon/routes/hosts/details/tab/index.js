import Route from '@ember/routing/route';

export default Route.extend({
  actions: {
    analyzeFile(fileHash, fileFormat, fileSid) {
      this.transitionTo('hosts.details.tab.fileanalysis', fileHash, fileFormat, fileSid);
    }
  }

});
