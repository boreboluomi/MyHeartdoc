#ifndef __TYPES_H__
#define __TYPES_H__

typedef unsigned char KS_u8;
typedef unsigned short KS_u16;
typedef unsigned int KS_u32;
typedef void KS_void;
typedef char KS_c8;
typedef short KS_s16;
typedef int KS_i32;
#define KS_NULL                ((void*)0)
typedef unsigned char UINT8;
typedef signed char INT8;
typedef unsigned short UINT16;
typedef signed short INT16;
typedef unsigned long UINT32;

typedef float FP32;
typedef double FP64;

typedef unsigned int OS_STK;
typedef unsigned int OS_CPU_SR;

typedef unsigned int U32;
typedef unsigned short int U16;
typedef int S32;
typedef short int S16;
typedef unsigned char U8;
typedef char S8;

#ifndef CHAR
typedef char CHAR;
#endif
typedef unsigned char UCHAR;
typedef int INT32;

typedef unsigned int UINT;
typedef float FLOAT;

#define  FALSE 0
#define  TRUE  1
typedef unsigned char BOOL;

#endif
