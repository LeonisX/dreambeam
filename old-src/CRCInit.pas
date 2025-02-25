unit CRCinit;

interface

uses classes, sysutils, unit2, forms;

function GetNewCRC(OldCRC: cardinal; StPtr: pointer; StLen: integer): cardinal;
procedure UpdateCRC(StPtr: pointer; StLen: integer; var CRC: cardinal);
function GetFileCRC(const FileName: string): cardinal;
procedure handleexceptionio;

implementation


var
  CRCtable: array[0..255] of cardinal;

procedure handleexceptionio;
begin
zflag:=true;
end;

function GetNewCRC(OldCRC: cardinal; StPtr: pointer; StLen: integer): cardinal;
asm
  test edx,edx;
  jz @ret;
  neg ecx;
  jz @ret;
  sub edx,ecx; // Address after last element

  push ebx;
  mov ebx,0; // Set ebx=0 & align @next
@next:
  mov bl,al;
  xor bl,byte [edx+ecx];
  shr eax,8;
  xor eax,cardinal [CRCtable+ebx*4];
  inc ecx;
  jnz @next;
  pop ebx;

@ret:
end;

procedure UpdateCRC(StPtr: pointer; StLen: integer; var CRC: cardinal);
begin
  CRC := GetNewCRC(CRC, StPtr, StLen);
end;

function GetFileCRC(const FileName: string): cardinal;
const
  BufSize = 64 * 1024;
var
  Fi: file;
  pBuf: PChar;
  Count,res: cardinal;
begin
zflag:=false;
FileMode:=0;
zsize:=0;
  Assignfile(Fi, FileName);
  application.ProcessMessages;
  Reset(Fi, 1);
  GetMem(pBuf, BufSize);
  application.processmessages;
  Result := $FFFFFFFF;
  repeat
{$I-}
    BlockRead(Fi, pBuf^, BufSize, Count);
{$I+}
res := IOResult;
if res <> 0 then zflag:=true;

if (zflag=true) or (zbreak=true) then break;
  zsize:=zsize+bufsize;
  application.processmessages;
    if Count = 0 then
      break;
    Result := GetNewCRC(Result, pBuf, Count);
  until false;
  Result := not Result;
  application.ProcessMessages;
  FreeMem(pBuf);
  CloseFile(Fi);
end;


procedure CRCInits;
var
  c: cardinal;
  i, j: integer;
begin
  for i := 0 to 255 do
  begin
    c := i;
    for j := 1 to 8 do
      if odd(c) then
        c := (c shr 1) xor $EDB88320
      else
        c := (c shr 1);
    CRCtable[i] := c;
  end;
end;

initialization
  CRCinits;

end.