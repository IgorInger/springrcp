HibernateValidator hibValidator = ...
RulesValidator rulesValidator = ...
CompositeRichValidator validator = new CompositeRichValidator(new RichValidator[] { hibValidator, rulesValidator });