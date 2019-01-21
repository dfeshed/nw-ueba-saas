export default {
  flags: 1073938433,
  params: {
    msg: '<string, optional, {char:abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ?}> The name of the message to retrieve detailed help about (aliases are \'m\' or \'message\')',
    op: '<string, optional, {enum-one:messages|parameters|description|values|roles|extra|manual}> The specific help operation to perform (e.g., op=manual would return a man page on this node or the specified message)',
    format: '<string, optional, {enum-one:default|xml|html}> The format of the response, default returns in a human friendly format'
  },
  path: '/'
};