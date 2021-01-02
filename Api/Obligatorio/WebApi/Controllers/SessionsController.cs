using System;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using BusinessLogic;
using BusinessLogic.Interface;
using DataAccess;
using Domain;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.IdentityModel.Tokens;
using WebApi.Models;

namespace WebApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class SessionsController : Controller
    {
        private ISessionLogic Sessions;
        private IConfiguration _config;

        public SessionsController(IConfiguration config) : base()
        {
            DbContext context = ContextFactory.GetNewContext(ContextType.SQL);
            UserRepository repository = new UserRepository(context);
            Sessions = new SessionLogic(repository);
            _config = config;
        }

        [Route("signup")]
        [AllowAnonymous]
        [HttpPost]
        public IActionResult PostSignUp([FromBody] UserModel userFromRequest)
        {
            try
            {
                var user = UserModel.ToModel(Sessions.SignUp(UserModel.ToEntity(userFromRequest)));
                return Ok(user);
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [Route("signin")]
        [AllowAnonymous]
        [HttpPost]
        public IActionResult PostSignIn([FromBody] SignInModel credentials)
        {
            try
            {
                User user = Sessions.SignIn(credentials.Email, credentials.Password);
                var token = GenerateJSONWebToken(user);
                return Json(new
                {
                    uid = user.Email,
                    token
                });
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        private string GenerateJSONWebToken(User userInfo)
        {
            var securityKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_config["Jwt:Key"]));
            var credentials = new SigningCredentials(securityKey, SecurityAlgorithms.HmacSha256);

            var claims = new[] {
                new Claim(JwtRegisteredClaimNames.Email, userInfo.Email)
            };

            var token = new JwtSecurityToken(_config["Jwt:Issuer"],
              _config["Jwt:Issuer"],
              claims,
              expires: DateTime.Now.AddMinutes(120),
              signingCredentials: credentials);

            return new JwtSecurityTokenHandler().WriteToken(token);
        }
    }
}