import {Injectable} from "@angular/core";
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {catchError, Observable, switchMap, throwError} from "rxjs";
import {environment} from "../../environments/environment";
import {UserService} from "../shared/services/user.service";

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private userService: UserService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if(request.headers.get('skip') === 'true'){
      const newHeaders = request.headers.delete("skip");
      const newRequest= request.clone({headers: newHeaders});
      return next.handle(newRequest);
    }
    const isApiUrl = request.url.startsWith(environment.apiUrl);
    const isLoggedIn = this.userService.isLoggedIn;
    let at = this.userService.getAccessToken();
    let isAccessTokenValid = at && this.userService.isTokenValid(at);
    if(isLoggedIn && isApiUrl) {
      if (!isAccessTokenValid) {
        return this.userService.refreshTokens()
          .pipe(
            switchMap(() => {
              at = this.userService.getAccessToken();
              request = request.clone({
                setHeaders: {
                  Authorization: `Bearer ${at}`
                }
              });
              return next.handle(request);
            }),
            catchError(error => {
              console.log(error);
              this.userService.logout();
              return throwError(() => error);
            })
          );
      } else {
        request = request.clone({
          setHeaders: {
            Authorization: `Bearer ${at}`
          }
        });
        return next.handle(request);
      }
    } else {
      return next.handle(request)
    }
  }
}
