class DagsFactory:
    factories = {}

    def addFactory(id, dagsFactory):
        """
        adding factory will enable you to create it's dags, as part of the dags creation flow
        :param id: id of the factory
        :param dagsFactory: instance of dag factory  
        """
        DagsFactory.factories[id] = dagsFactory

    addFactory = staticmethod(addFactory)

    def createDags(id, **kwargs):
        """
        create dags for specified dag factory
        :param id: the id of the dag factory
        :param kwargs: 
        :return: 
        """
        if not DagsFactory.factories.has_key(id):
            DagsFactory.factories[id] = eval(id + '.Factory()')
        return DagsFactory.factories[id].create_and_register_dags(**kwargs)

    createDags = staticmethod(createDags)
