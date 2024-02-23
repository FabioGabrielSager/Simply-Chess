import {CanActivateFn, Router} from '@angular/router';
import {SessionService} from "../../services/session.service";
import {inject} from "@angular/core";
import {ToastService} from "../../services/toast.service";
import {catchError, map, of} from "rxjs";

export const authGuard: CanActivateFn = (route, state) => {
  const sessionService: SessionService = inject(SessionService);
  const router: Router = inject(Router);
  const toastService: ToastService = inject(ToastService);

  return sessionService.isLogged().pipe(
    map((isLoggedIn: boolean) => {
      if (isLoggedIn) {
        return true;
      } else {
        toastService.show("Debe iniciar sesión primero", "bg-danger");
        return router.parseUrl('/home');
      }
    }),
    catchError(err => {return of(router.parseUrl('/home'))})
  );
};
