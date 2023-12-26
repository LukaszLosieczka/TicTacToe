import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {UserService} from "../../../shared/services/user.service";
import {Router} from "@angular/router";
import {User} from "../../model/User";
import {first} from "rxjs";

@Component({
  selector: 'bs-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  form: FormGroup;
  loading = false;
  submitted = false;
  isError = false;
  errorMessage = "";

  constructor(private userService: UserService, private router: Router) { }

  ngOnInit(): void {
    this.form = new FormGroup({
      email: new FormControl("", [Validators.required, Validators.email]),
      username: new FormControl("", [Validators.required]),
      password: new FormControl("", [Validators.required, Validators.minLength(6)])
    });
  }

  get f() { return this.form.controls; }

  onSubmit(): void{
    this.submitted = true;

    if (this.form.invalid) {
      return;
    }

    const email = this.form.get("email")?.value
    const login = this.form.get("username")?.value
    const password = this.form.get("password")?.value

    const user: User = {email, login, password}

    this.loading = true;
    this.userService.register(user)
      .pipe(first())
      .subscribe({
        next: () => {
          this.router.navigate(['confirm']);
        },
        error: error => {
          this.isError = true;
          this.errorMessage = error.error;
          console.log(error);
          this.loading = false;
        }
      });
  }

}

