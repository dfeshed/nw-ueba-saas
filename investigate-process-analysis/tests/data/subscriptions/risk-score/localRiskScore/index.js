export default {
  subscriptionDestination: '/user/queue/respond/risk/local/score',
  requestDestination: '/ws/respond/risk/local/score',
  message(/* frame */) {
    return {
      data: [
        {
          id: '64a49ca7b214ae8905345b785b13a3c6',
          score: 99
        },
        {
          id: '07d15ddf2eb7be486d01bcabab7ad8df35b7942f25f5261e3c92cd7a8931190c',
          score: 100
        }
      ]
    };
  }
};
