/**
 * @description creates mock API route for users and profile related apis.
 * @public
 */

export default function(config) {
  config.get('/response/api/users', function(db) {
    return {
      code: 0,
      data: db.users
    };
  });
}
