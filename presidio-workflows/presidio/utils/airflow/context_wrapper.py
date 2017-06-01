class ContextWrapper(object):
    """
    ContextWrapper responsible for getting data of context
    """

    def __init__(self,context):
        self._context = context

    def get_execute_date(self):
        return self._context['execution_date']
