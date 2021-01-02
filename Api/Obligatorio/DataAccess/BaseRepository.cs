using DataAccess.Interface;
using DataAccess.Interface.Exceptions;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Security.Cryptography;
using System.Text;

namespace DataAccess
{
    public abstract class BaseRepository<T> : IRepository<T> where T : class
    {
        public const string BDCONNECTIONERROR = "Imposible conectar con la BD. Asegúrese de que los servicios estén activos.";
        protected DbContext Context { get; set; }

        public abstract void MigrateDB();

        public virtual T Add(T entity)
        {
            try
            {
                T e = Context.Set<T>().Add(entity).Entity;
                Save();
                return e;
            }
            catch (SqlException ex)
            {
                throw new DatabaseError(BDCONNECTIONERROR, ex);
            }
        }

        public void Remove(T entity)
        {
            try
            {
                Context.Set<T>().Remove(entity);
            }
            catch (SqlException ex)
            {
                throw new DatabaseError(BDCONNECTIONERROR, ex);
            }
        }

        public virtual T Update(T entity)
        {
            try
            {
                Context.Entry(entity).State = EntityState.Modified;
                Save();
                return entity;
            }
            catch (SqlException ex)
            {
                throw new DatabaseError(BDCONNECTIONERROR, ex);
            }
        }

        public abstract List<T> GetAll();

        public abstract T Get(Guid id);

        public abstract bool Exists(Guid id);

        public void Save()
        {
            try
            {
                Context.SaveChanges();
            }
            catch (SqlException ex)
            {
                throw new DatabaseError(BDCONNECTIONERROR, ex);
            }
        }

        #region IDisposable Support
        private bool disposedValue = false;

        protected virtual void Dispose(bool disposing)
        {
            if (!disposedValue)
            {
                if (disposing)
                {
                    try
                    {
                        Context.Dispose();
                    }
                    catch (SqlException ex)
                    {
                        throw new DatabaseError(BDCONNECTIONERROR, ex);
                    }
                }
                disposedValue = true;
            }
        }

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }
        #endregion
    }
}
