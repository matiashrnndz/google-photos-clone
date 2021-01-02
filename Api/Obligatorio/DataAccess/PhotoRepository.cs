using DataAccess.Interface.Exceptions;
using Domain;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Text;

namespace DataAccess
{
    public class PhotoRepository : BaseRepository<Photo>
    {
        public PhotoRepository(DbContext context)
        {
            Context = context;
            MigrateDB();
        }

        public override void MigrateDB()
        {
            if (Context.Database.IsSqlServer())
            {
                Context.Database.EnsureCreated();
            }
        }

        public override bool Exists(Guid id)
        {
            try
            {
                List<Photo> list = GetAll();
                return list.Exists(x => x.Id == id);
            }
            catch (SqlException ex)
            {
                throw new DatabaseError(BDCONNECTIONERROR, ex);
            }
        }

        public override Photo Get(Guid id)
        {
            try
            {
                Photo photo = Context.Set<Photo>()
               .Include(p => p.Tags)
               .Where(x => x.Id == id)
               .First();
                return photo;
            }
            catch (SqlException ex)
            {
                throw new DatabaseError(BDCONNECTIONERROR, ex);
            }
        }

        public override List<Photo> GetAll()
        {
            try
            {
                return Context.Set<Photo>().Include(p => p.Tags).ToList();
            }
            catch (SqlException ex)
            {
                throw new DatabaseError(BDCONNECTIONERROR, ex);
            }
        }
    }
}
