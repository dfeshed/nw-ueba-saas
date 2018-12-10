export default [].concat(
  [
    {
      type: 'host',
      threshold: 75,
      timeWindow: '1d'
    },
    {
      type: 'file',
      threshold: 80,
      timeWindow: '24h'
    }
  ]
);
