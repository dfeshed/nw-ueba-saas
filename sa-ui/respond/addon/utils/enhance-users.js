/**
 * Move the current user to the front of the users list, and format as 'Myself ( John Doe )'.
 * Note: users is a mutable array from a redux selector, so make a copy.
 *
 * @param users The assignees we can filter by
 * @param loggedInUsername The username of the current logged-in user
 */
export default function(users, loggedInUsername) {
  if (users) {
    const ind = users.findIndex((elem) => elem.id === loggedInUsername);
    if (ind !== -1) {
      const copy = [...users];
      const loggedInEntity = users[ind];
      copy[ind] = { ...loggedInEntity, name: `Myself (${loggedInEntity.name || loggedInEntity.id})` };
      const myself = copy.splice(ind, 1);
      copy.unshift(myself[0]);
      return copy;
    }
  }
  return users;
}