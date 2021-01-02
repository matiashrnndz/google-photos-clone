using System;
using Microsoft.EntityFrameworkCore.Migrations;

namespace DataAccess.Migrations
{
    public partial class AddAlbums : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<Guid>(
                name: "AlbumId",
                table: "Photo",
                nullable: true);

            migrationBuilder.CreateTable(
                name: "Album",
                columns: table => new
                {
                    Id = table.Column<Guid>(nullable: false),
                    Name = table.Column<string>(nullable: true),
                    UserId = table.Column<Guid>(nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Album", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Album_Users_UserId",
                        column: x => x.UserId,
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Restrict);
                });

            migrationBuilder.CreateIndex(
                name: "IX_Photo_AlbumId",
                table: "Photo",
                column: "AlbumId");

            migrationBuilder.CreateIndex(
                name: "IX_Album_UserId",
                table: "Album",
                column: "UserId");

            migrationBuilder.AddForeignKey(
                name: "FK_Photo_Album_AlbumId",
                table: "Photo",
                column: "AlbumId",
                principalTable: "Album",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Photo_Album_AlbumId",
                table: "Photo");

            migrationBuilder.DropTable(
                name: "Album");

            migrationBuilder.DropIndex(
                name: "IX_Photo_AlbumId",
                table: "Photo");

            migrationBuilder.DropColumn(
                name: "AlbumId",
                table: "Photo");
        }
    }
}
