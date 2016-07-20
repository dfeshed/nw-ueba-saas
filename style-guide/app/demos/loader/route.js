import Ember from 'ember';
const { Route } = Ember;

export default Route.extend({

  model() {
    return {
      'title': 'Loader',
      'subtitle': 'RSA loader component examples',
      'description': 'Below is a list of implementation examples for the RSA loader. Please note that while all of the sizes featured below can use the label attribute, only the top four sizes should make use of it.',
      'testFilter': 'rsa-loader'
    };
  }

});
