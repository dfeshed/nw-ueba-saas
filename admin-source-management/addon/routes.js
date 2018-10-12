import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.route('groups');
  this.route('group-create', { path: 'group/create' });
  this.route('group-wizard', { path: 'group/wizard/:group_id' });
  this.route('group-ranking', { path: 'group/ranking' });
  this.route('policies');
  this.route('policy-create', { path: 'policy/create' });
  this.route('policy-wizard', { path: 'policy/wizard/:policy_id' });
});
