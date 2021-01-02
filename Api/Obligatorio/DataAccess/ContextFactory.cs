
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Design;
using System;
using System.Collections.Generic;
using System.Text;

namespace DataAccess
{
    public enum ContextType
    {
        MEMORY, SQL
    }

    public class ContextFactory : IDesignTimeDbContextFactory<AppContext>
    {
        public AppContext CreateDbContext(string[] args)
        {
            return GetNewContext();
        }

        public static AppContext GetNewContext(ContextType type = ContextType.SQL)
        {
            if (type == ContextType.SQL)
            {
                if (Environment.GetEnvironmentVariable("ASPNETCORE_ENVIRONMENT") == "Production")
                {
                    return GetSqlContext(@"Server=tcp:sqlobligatorio.database.windows.net,1433;Database=coreDB;User ID=obligatorio;Password=UniversidadORT19;Encrypt=true;Connection Timeout=30;");
                }
                else
                {
                    return GetSqlContext(@"Server=.\SQLEXPRESS;Database=ObligatorioDB;Trusted_Connection=True;MultipleActiveResultSets=True;");
                }
            }
            return GetMemoryContext("ObligatorioDB");
        }

        public static AppContext GetNewContextForTesting(ContextType type = ContextType.SQL)
        {
            if (type == ContextType.SQL)
            {
                return GetSqlContext(@"Server=.\SQLEXPRESS;Database=ObligatorioDBTest;Trusted_Connection=True;MultipleActiveResultSets=True;");
            }
            return GetMemoryContext("ObligatorioDBTest");
        }

        private static AppContext GetMemoryContext(string connection)
        {
            var builder = new DbContextOptionsBuilder<AppContext>();
            builder.UseInMemoryDatabase(connection);
            return new AppContext(builder.Options);
        }

        private static AppContext GetSqlContext(string connection)
        {
            var builder = new DbContextOptionsBuilder<AppContext>();
            builder.UseSqlServer(connection);
            return new AppContext(builder.Options);
        }
    }
}
