import Controller from '@ember/controller';

export default Controller.extend({
  // Query Params
  queryParams: ['query'],

  query: null,

  actions: {
    controllerAnalyzeFile(fileHash, fileFormat, fileSid) {
      this.send('analyzeFile', fileHash, fileFormat, fileSid);
    }
  }

});
