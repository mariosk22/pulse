import { routes } from './../../../app.routes';
import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder,ReactiveFormsModule,Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { email } from '@angular/forms/signals';

@Component({
  selector: 'app-login',
  standalone:true,
  imports: [ ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  form = this.fb.group({
    email:['',[Validators.required,Validators.email]],
    password:['',Validators.required]
  });
errorMessage ='';


OnSubmit():void{
  if (this.form.invalid)return;
  this.authService.login(this.form.getRawValue()as any).subscribe({
    next:()=>this.router.navigateByUrl('/dashboard'),
    error:(err)=> this.errorMessage = err.error?.message?? 'Login Failed '

  });
}
}
