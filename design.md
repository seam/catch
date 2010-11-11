1. handler methods that return void, accept ExceptionHandlingEvent<T extends Throwable> annotated @Handles as first parameter in a bean annotated @HandlesExceptions
2. handler methods that handle the same type have to be disambiguated by a precedence attribute on @Handles
3.

        ExceptionHandlingEvent {
           getStackInfo() -> StackInfo { root, last, index, nextCause, remainingCauses, elements }
           handled();
           proceed();
           proceedToCause();
           abort();
           getException();
           mute();
        }
4. exception stack is unwrapped, handled from inner to outer
5. for the current cause, match handlers for each type in hierarchy and invoke in most specific to least specific order unless weight is given
  Exception(W100) => MyException(P100) => MyException(P50) = Throwable(W0)
  default weight and precedence are 0
6. handler classes are 100%, bonafide beans
7. ExceptionBindingType can be used to bind an exception to a handler
8. A new instancef of ExceptionHandlingEvent will be created for each step in (new EHE) Exception(W100) => (new EHE) MyException(P100) => (new EHE) MyException(P50) = (new EHE) Throwable(W0)
9. CDI extension for ProcessBean<X> phase
10. and then AfterDeploymentValidation we can do some post-processing

what you need to do in ProcessBean is store the methods in a registry that's application scoped
all extensions are application-scoped, so just a field


##Future ideas
*  Activations:

        @AtNight
        public class NighttimeExceptionHandlerActivation implements ExceptionHandlerActivation {
           public boolean handles(LoginException e) {
           }
        }

*  tie the JMS bridge into this to make exception handling async
