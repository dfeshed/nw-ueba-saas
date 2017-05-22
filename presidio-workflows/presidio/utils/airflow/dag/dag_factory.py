
class Singleton(type):
    _instances = {}
    def __call__(cls, *args, **kwargs):
        if cls not in cls._instances:
            cls._instances[cls] = super(Singleton, cls).__call__(*args, **kwargs)
        return cls._instances[cls]

class DagFactories:
    __metaclass__ = Singleton
    factories = {}

    def add_factory(factory_id, dag_factory_instance):
        """
        adding factory will enable you to create it's dags, as part of the dags creation flow
        :param factory_id: id of the factory
        :param dag_factory_instance: instance of dag factory  
        """
        DagFactories.factories[factory_id] = dag_factory_instance

    add_factory = staticmethod(add_factory)

    def create_dags(factory_id, **kwargs):
        """
        create dags for specified dag factory
        :param factory_id: the id of the dag factory
        :param kwargs: 
        :return: 
        """
        return DagFactories.factories[factory_id].create_and_register_dags(**kwargs)

    create_dags = staticmethod(create_dags)
